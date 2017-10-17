package com.kingsoft.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kingsoft.Constants;
import com.kingsoft.utils.RedisTemplate;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Supplier;

/**
 * Created by SHIZHIDA on 2017/6/13.
 */
@Service
public class RedisService {

    @Autowired
    RedisTemplate redisTemplate;

    @Autowired
    ObjectMapper objectMapper;

    /**
     * json转换成数组的映射
     * @param clazz
     * @return
     */
    public JavaType arrayType(Class clazz){
        return objectMapper.getTypeFactory().constructCollectionType(ArrayList.class,clazz);
    }

    /**
     * json转换成单个元素的映射
     * @param clazz
     * @return
     */
    public JavaType classType(Class clazz){
        return objectMapper.getTypeFactory().constructType(clazz);
    }

    /**
     * 获取hash中的元素并转换，若不存在则从supplier中获取
     * @param cache
     * @param key
     * @param supplier
     * @param jsonBind
     * @param <T>
     * @return
     */
    public <T> T hgetOr(String cache, String key, Supplier<T> supplier,JavaType jsonBind){
        String json = redisTemplate.hget(cache,key);
        T result = null;
        if(StringUtils.isNotEmpty(json)){
            try {
                result = objectMapper.readValue(json,jsonBind);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if(result==null){
            result = supplier.get();
            hset(cache,key,result);
            redisTemplate.expire(cache, Constants.REDIS_EXPIRE_TIME_ONE_HOUR);
        }
        return result;
    }

    /**
     * 获取hash中的数组并转换，若不存在则从supplier中获取
     */
    public <T> List<T> hgetListOr(String cache, String key, Supplier<List<T>> supplier, JavaType jsonBind){
        List<T> result = hgetList(cache,key,jsonBind);
        if(result==null){
            result = supplier.get();
            hset(cache,key,result);
            redisTemplate.expire(cache, Constants.REDIS_EXPIRE_TIME_ONE_HOUR);
        }
        return result;
    }

    /**
     * 将元素添加到json形式的数组中
     * @param cache
     * @param key
     * @param item
     * @param <T>
     * @return
     */
    public <T> boolean hAppendList(String cache,String key,T item){
        List<T> result = hgetList(cache,key,arrayType(item.getClass()));
        if(result==null){
            return false;
        }
        result.add(item);
        hset(cache,key,result);
        return true;
    }

    /**
     * 将元素替换到json形式的数组中，若不存在此元素则添加一个
     * @param cache
     * @param key
     * @param newItem
     * @param comparator
     * @param <T>
     * @return
     */
    public <T> boolean hReplaceList(String cache, String key, T newItem, Comparator<T> comparator){
        List<T> result = hgetList(cache,key,arrayType(newItem.getClass()));
        if(result==null){
            return false;
        }
        boolean hasUpdate = false;
        for (T t : result) {
            if(comparator.compare(t,newItem)==0){
                BeanUtils.copyProperties(newItem,t);
                hasUpdate = true;
                break;
            }
        }
        if(!hasUpdate)
            result.add(newItem);
        hset(cache,key,result);
        return true;
    }


    /**
     * 获取json数组
     * @param cache
     * @param key
     * @param jsonBind
     * @param <T>
     * @return
     */
    public <T> List<T> hgetList(String cache,String key,JavaType jsonBind){
        String json = redisTemplate.hget(cache,key);
        List<T> result = null;
        if(StringUtils.isNotEmpty(json)){
            try {
                result = objectMapper.readValue(json,jsonBind);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if(result==null){
            return  null;
        }
        return result.size()==0?null:result;
    }

    /**
     * 将数据插入到某个Hash的key中
     * @param cache
     * @param key
     * @param obj
     */
    public void hset(String cache,String key,Object obj){
        try {
            redisTemplate.hset(cache,key, objectMapper.writeValueAsString(obj));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

}
