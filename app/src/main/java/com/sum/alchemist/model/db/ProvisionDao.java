package com.sum.alchemist.model.db;

import com.sum.alchemist.model.entity.Provision;

/**
 * PushMsg DAO
 * Created by TUS on 2016/5/24.
 */
public class ProvisionDao extends BaseDaoImpl<Provision> {

    private static final String TAG = "ProvisionDao";

    public ProvisionDao(){
        super(Provision.class);
    }


}
