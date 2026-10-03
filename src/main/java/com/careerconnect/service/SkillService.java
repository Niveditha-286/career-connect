package com.careerconnect.service;

import com.careerconnect.entity.Skill;
import com.careerconnect.entity.User;
import com.careerconnect.repository.SkillRepository;
import com.careerconnect.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

@Service
public class SkillService {

    private final UserRepository userRepository;
    private final SkillRepository skillRepository;

    public SkillService(
            UserRepository userRepository,
            SkillRepository skillRepository) {

        this.userRepository = userRepository;
        this.skillRepository = skillRepository;
    }

    @Transactional
    public Skill addSkill(String email, String skillName) {

        User user = userRepository.findByEmail(email)
                .orElseThrow();

        Skill skill = skillRepository
                .findByNameIgnoreCase(skillName.trim())
                .orElseGet(() ->
                        skillRepository.save(
                                new Skill(skillName.trim())
                        )
                );

        user.getSkills().add(skill);

        userRepository.save(user);

        return skill;
    }

    @Transactional
    public Set<Skill> getMySkills(String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow();

        return new HashSet<>(user.getSkills());
    }

    @Transactional
    public void removeSkill(String email, Long skillId) {

        User user = userRepository.findByEmail(email)
                .orElseThrow();

        user.getSkills().removeIf(
                skill -> skill.getId().equals(skillId)
        );

        userRepository.save(user);
    }
}