/**
 * 
 */
package com.mmh.vmma.jpa.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.mmh.vmma.jpa.entities.SystemConfig;

/**
 * @author hongftan
 *
 */
@Repository
public interface SystemConfigRepository extends JpaRepository<SystemConfig, String> {

}
