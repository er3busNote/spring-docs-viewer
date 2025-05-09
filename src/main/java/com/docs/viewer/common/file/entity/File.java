package com.docs.viewer.common.file.entity;

import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import javax.persistence.*;

@Entity
@Table(name = "CMM_FILE")   // 상품
@Getter @Setter
@EqualsAndHashCode(of = "fileCode")
@Builder @NoArgsConstructor
@AllArgsConstructor
public class File {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "fileCode")
    @GenericGenerator(name = "fileCode", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator",
            parameters = {
                    @Parameter(name = "sequence_name", value = "CMM_FILE_SEQUENCE"),
                    @Parameter(name = "initial_value", value = "100000"),
                    @Parameter(name = "increment_size", value = "1")
            })
    @Column(name = "FILE_CD", columnDefinition = "BIGINT(20) COMMENT '파일코드'")
    private Long fileCode;

    @Column(name = "FILE_NM", columnDefinition = "VARCHAR(100) COMMENT '파일명'")
    private String fileName;

    @Column(name = "FILE_PATH", columnDefinition = "VARCHAR(200) COMMENT '파일경로'")
    private String filePath;

    @Column(name = "FILE_TYPE", columnDefinition = "VARCHAR(100) COMMENT '파일유형'")
    private String fileType;

    @Column(name = "FILE_SIZE", columnDefinition = "INT(11) COMMENT '파일크기'")
    private Integer fileSize;

    @Builder.Default
    @Column(name = "CLOUD_YN", nullable = false, columnDefinition = "CHAR(1) COMMENT '버킷사용여부'")
    private Character cloudYn = 'N';

    public static File of(String fileName, String filePath, String fileType, Integer fileSize, Character cloudYn) {
        return File.builder()
                .fileName(fileName)
                .filePath(filePath)
                .fileType(fileType)
                .fileSize(fileSize)
                .cloudYn(cloudYn)
                .build();
    }
}
