package com.sum.alchemist.model.db;

import com.sum.alchemist.model.entity.Requirement;

/**
 * PushMsg DAO
 * Created by TUS on 2016/5/24.
 */
public class RequirementDao extends BaseDaoImpl<Requirement> {

    private static final String TAG = "RequirementDao";

    public RequirementDao(){
        super(Requirement.class);
    }


}
