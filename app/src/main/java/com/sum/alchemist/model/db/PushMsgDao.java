package com.sum.alchemist.model.db;

import com.sum.alchemist.model.entity.PushMsg;

/**
 * PushMsg DAO
 * Created by TUS on 2016/5/24.
 */
public class PushMsgDao extends BaseDaoImpl<PushMsg> {

    private static final String TAG = "PushMsgDao";

    public PushMsgDao(){
        super(PushMsg.class);
    }

}
