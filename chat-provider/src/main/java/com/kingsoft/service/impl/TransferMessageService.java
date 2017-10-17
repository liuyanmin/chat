package com.kingsoft.service.impl;

import com.kingsoft.Contants;
import com.kingsoft.service.ITransferMessage;
import com.kingsoft.utils.HttpUtil;
import com.kingsoft.utils.ServerUtil;


/**
 * Created by LIUYANMIN on 2017/10/16.
 */
public class TransferMessageService implements ITransferMessage {

    @Override
    public Object transferTextMsg(String message) {
        String port = (String) ServerUtil.getEndPoints().get("port");

        String url = Contants.LOCAL_URL_PREFIX + port + Contants.LOCAL_URL_SUFFIX + message;
        String str = HttpUtil.request(url, null);
        return Contants.OK;
    }

}
