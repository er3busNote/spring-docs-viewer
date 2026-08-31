package com.docs.viewer.common.preview.repository;

import com.docs.viewer.common.preview.entity.Preview;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PreviewRepository extends JpaRepository<Preview, Long> {
}
