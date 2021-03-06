/*
 * The contents of this file are subject to the OpenMRS Public License
 * Version 2.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */
package org.openmrs.module.openhmis.cashier.api.impl;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;
import org.openmrs.Location;
import org.openmrs.api.APIException;
import org.openmrs.module.openhmis.cashier.api.ICashPointService;
import org.openmrs.module.openhmis.cashier.api.model.CashPoint;
import org.openmrs.module.openhmis.cashier.api.security.BasicMetadataAuthorizationPrivileges;
import org.openmrs.module.openhmis.cashier.api.util.HibernateCriteriaConstants;
import org.openmrs.module.openhmis.commons.api.PagingInfo;
import org.openmrs.module.openhmis.commons.api.entity.impl.BaseMetadataDataServiceImpl;
import org.openmrs.module.openhmis.commons.api.entity.security.IMetadataAuthorizationPrivileges;
import org.openmrs.module.openhmis.commons.api.f.Action1;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
public class CashPointServiceImpl
		extends BaseMetadataDataServiceImpl<CashPoint>
		implements ICashPointService {
	@Override
	protected IMetadataAuthorizationPrivileges getPrivileges() {
		return new BasicMetadataAuthorizationPrivileges();
	}

	@Override
	protected void validate(CashPoint entity) throws APIException {
	}

    @Override
    public List<CashPoint> getCashPointsByLocation(Location location, boolean includeRetired) throws APIException {
        return getCashPointsByLocation(location, includeRetired, null);
    }

    @Override
    public List<CashPoint> getCashPointsByLocation(final Location location, final boolean includeRetired, PagingInfo pagingInfo) throws APIException {
        if (location == null) {
            throw new IllegalArgumentException("The location must be defined");
        }

        return executeCriteria(CashPoint.class, pagingInfo, new Action1<Criteria>() {
            @Override
            public void apply(Criteria criteria) {
                criteria.add(Restrictions.eq(HibernateCriteriaConstants.LOCATION, location));
                if (!includeRetired) {
                    criteria.add(Restrictions.eq(HibernateCriteriaConstants.RETIRED, false));
                }
            }
        });
    }

    @Override
    public List<CashPoint> findCashPoints(Location location, String name, boolean includeRetired) throws APIException {
        return findCashPoints(location, name, includeRetired, null);
    }

    @Override
    public List<CashPoint> findCashPoints(final Location location, final String name, final boolean includeRetired, PagingInfo pagingInfo) throws APIException {
        if (location == null) {
            throw new IllegalArgumentException("The location must be defined");
        }
        if (StringUtils.isEmpty(name)) {
            throw new IllegalArgumentException("The Cashpoint name must be defined.");
        }
        if (name.length() > 255) {
            throw new IllegalArgumentException("The Cashpoint name must be less than 256 characters.");
        }

        return executeCriteria(CashPoint.class, pagingInfo, new Action1<Criteria>() {
            @Override
            public void apply(Criteria criteria) {
                criteria.add(Restrictions.eq(HibernateCriteriaConstants.LOCATION, location))
                        .add(Restrictions.ilike(HibernateCriteriaConstants.NAME, name, MatchMode.START));

                if (!includeRetired) {
                    criteria.add(Restrictions.eq(HibernateCriteriaConstants.RETIRED, false));
                }
            }
        });
    }
}

