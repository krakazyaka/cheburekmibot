package com.cheburekmi.cheburek.service;

import com.cheburekmi.cheburek.dto.AddonDto;
import com.cheburekmi.cheburek.entity.Addon;
import com.cheburekmi.cheburek.mapper.AddonMapper;
import com.cheburekmi.cheburek.repository.AddonRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AddonService {
    private final AddonRepository addonRepository;
    private final AddonMapper addonMapper;

    @Transactional(readOnly = true)
    public List<AddonDto> getAllAddons() {
        List<Addon> addons = addonRepository.findAll();
        return addonMapper.toDtoList(addons);
    }

    @Transactional(readOnly = true)
    public List<AddonDto> getAvailableAddons() {
        List<Addon> addons = addonRepository.findByAvailableTrue();
        return addonMapper.toDtoList(addons);
    }

    @Transactional(readOnly = true)
    public AddonDto getAddonById(Long id) {
        Addon addon = addonRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Addon not found with id: " + id));
        return addonMapper.toDto(addon);
    }

    @Transactional
    public AddonDto createAddon(AddonDto addonDto) {
        Addon addon = addonMapper.toEntity(addonDto);
        Addon savedAddon = addonRepository.save(addon);
        return addonMapper.toDto(savedAddon);
    }

    @Transactional
    public AddonDto updateAddon(Long id, AddonDto addonDto) {
        Addon existingAddon = addonRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Addon not found with id: " + id));
        
        existingAddon.setName(addonDto.getName());
        existingAddon.setDescription(addonDto.getDescription());
        existingAddon.setPrice(addonDto.getPrice());
        existingAddon.setImage(addonDto.getImage());
        existingAddon.setAvailable(addonDto.getAvailable());
        
        Addon updatedAddon = addonRepository.save(existingAddon);
        return addonMapper.toDto(updatedAddon);
    }

    @Transactional
    public void deleteAddon(Long id) {
        addonRepository.deleteById(id);
    }
}
