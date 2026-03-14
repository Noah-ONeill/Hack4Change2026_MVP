package org.resourcebridge.api.service.impl;

import lombok.RequiredArgsConstructor;
import org.resourcebridge.api.entity.Need;
import org.resourcebridge.api.enums.Urgency;
import org.resourcebridge.api.exception.ResourceNotFoundException;
import org.resourcebridge.api.repository.NeedRepository;
import org.resourcebridge.api.service.NeedService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NeedServiceImpl implements NeedService {

    private final NeedRepository needRepository;

    @Override
    public List<Need> getAll() {
        return needRepository.findAll();
    }

    @Override
    public Need getById(Long id) {
        return needRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Need", id));
    }

    @Override
    public Need save(Need need) {
        return needRepository.save(need);
    }

    @Override
    public void deleteById(Long id) {
        needRepository.deleteById(id);
    }

    @Override
    public List<Need> findUnfulfilledNeeds() {
        return needRepository.findByFulfilled(false);
    }

    @Override
    public List<Need> findByOrganizationId(Long organizationId) {
        return needRepository.findByOrganizationId(organizationId);
    }

    @Override
    public List<Need> findUnfulfilledByOrganization(Long organizationId) {
        return needRepository.findByOrganizationIdAndFulfilled(organizationId, false);
    }

    @Override
    public List<Need> findByUrgency(Urgency urgency) {
        return needRepository.findByUrgencyAndFulfilled(urgency, false);
    }

    @Override
    public Need markFulfilled(Long needId) {
        Need need = getById(needId);
        need.setFulfilled(true);
        return needRepository.save(need);
    }
}
