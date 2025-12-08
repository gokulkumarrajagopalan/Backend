package com.tally.service;

import com.tally.entity.Group;
import com.tally.repository.GroupRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class GroupService {
    
    @Autowired
    private GroupRepository groupRepository;
    
    public List<Group> getAllGroups() {
        return groupRepository.findAll();
    }
    
    public Optional<Group> getGroupById(Long id) {
        return groupRepository.findById(id);
    }
    
    public Group createGroup(Group group) {
        return groupRepository.save(group);
    }
    
    public Group updateGroup(Long id, Group group) {
        group.setId(id);
        return groupRepository.save(group);
    }
    
    public void deleteGroup(Long id) {
        groupRepository.deleteById(id);
    }
    
    public List<Group> getGroupsByType(String type) {
        return groupRepository.findByType(type);
    }
}
