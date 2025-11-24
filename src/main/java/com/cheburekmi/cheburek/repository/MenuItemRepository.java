package com.cheburekmi.cheburek.repository;

import com.cheburekmi.cheburek.entity.MenuItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MenuItemRepository extends JpaRepository<MenuItem, Integer> {

    List<MenuItem> findAllByAvailableTrue();
}
