package com.cheburekmi.cheburek.controller;

import com.cheburekmi.cheburek.dto.AddonDto;
import com.cheburekmi.cheburek.service.AddonService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/addons")
@RequiredArgsConstructor
public class AddonController {
    private final AddonService addonService;

    @GetMapping
    public ResponseEntity<List<AddonDto>> getAllAddons() {
        return ResponseEntity.ok(addonService.getAllAddons());
    }

    @GetMapping("/available")
    public ResponseEntity<List<AddonDto>> getAvailableAddons() {
        return ResponseEntity.ok(addonService.getAvailableAddons());
    }

    @GetMapping("/{id}")
    public ResponseEntity<AddonDto> getAddonById(@PathVariable Long id) {
        return ResponseEntity.ok(addonService.getAddonById(id));
    }

    @PostMapping
    public ResponseEntity<AddonDto> createAddon(@RequestBody AddonDto addonDto) {
        AddonDto createdAddon = addonService.createAddon(addonDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdAddon);
    }

    @PutMapping("/{id}")
    public ResponseEntity<AddonDto> updateAddon(@PathVariable Long id, @RequestBody AddonDto addonDto) {
        AddonDto updatedAddon = addonService.updateAddon(id, addonDto);
        return ResponseEntity.ok(updatedAddon);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAddon(@PathVariable Long id) {
        addonService.deleteAddon(id);
        return ResponseEntity.noContent().build();
    }
}
