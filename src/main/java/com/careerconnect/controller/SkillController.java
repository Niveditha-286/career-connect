package com.careerconnect.controller;

import com.careerconnect.entity.Skill;
import com.careerconnect.service.SkillService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("/api/users/me/skills")
public class SkillController {

    private final SkillService skillService;

    public SkillController(SkillService skillService) {
        this.skillService = skillService;
    }

    @PostMapping
    public ResponseEntity<Skill> addSkill(
            Authentication authentication,
            @RequestParam String skillName) {

        String email = authentication.getName();

        Skill skill = skillService.addSkill(email, skillName);

        return ResponseEntity.ok(skill);
    }

    @GetMapping
    public ResponseEntity<Set<Skill>> getMySkills(
            Authentication authentication) {

        String email = authentication.getName();

        return ResponseEntity.ok(
                skillService.getMySkills(email)
        );
    }

    @DeleteMapping("/{skillId}")
    public ResponseEntity<Void> removeSkill(
            Authentication authentication,
            @PathVariable Long skillId) {

        String email = authentication.getName();

        skillService.removeSkill(email, skillId);

        return ResponseEntity.noContent().build();
    }
}