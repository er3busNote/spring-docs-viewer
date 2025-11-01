package com.docs.viewer.common.file.repository;

import com.docs.viewer.common.file.entity.File;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FileRepository extends JpaRepository<File, Long> {
}
