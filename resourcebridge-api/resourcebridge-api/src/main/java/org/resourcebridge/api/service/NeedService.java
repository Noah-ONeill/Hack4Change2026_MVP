package org.resourcebridge.api.service;

import org.resourcebridge.api.entity.Need;
import org.resourcebridge.api.enums.Urgency;

import java.util.List;

public interface NeedService extends GenericService<Need> {

    List<Need> findUnfulfilledNeeds();

    List<Need> findByOrganizationId(Long organizationId);

    List<Need> findUnfulfilledByOrganization(Long organizationId);

    List<Need> findByUrgency(Urgency urgency);

    Need markFulfilled(Long needId);
}
