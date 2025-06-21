package com.docs.viewer.common.file.entity;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

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

    @Column(name = "REGR", nullable = false, columnDefinition = "VARCHAR(30) COMMENT '등록자'")
    @NotBlank
    private String register;

    @Builder.Default
    @Column(name = "REG_DT", updatable = false, nullable = false, columnDefinition = "TIMESTAMP DEFAULT current_timestamp() COMMENT '등록일시'")
    @CreationTimestamp  // INSERT 시 자동으로 값을 채워줌
    private LocalDateTime registerDate = LocalDateTime.now();

    @Column(name = "UPDR", nullable = false, columnDefinition = "VARCHAR(30) COMMENT '수정자'")
    @NotBlank
    private String updater;

    @Builder.Default
    @Column(name = "UPD_DT", nullable = false, columnDefinition = "TIMESTAMP DEFAULT current_timestamp() ON UPDATE current_timestamp() COMMENT '수정일시'")
    @UpdateTimestamp    // UPDATE 시 자동으로 값을 채워줌
    private LocalDateTime updateDate = LocalDateTime.now();

    public static File of(String fileName, String filePath, String fileType, Integer fileSize, Character cloudYn) {
        return File.builder()
                .fileName(fileName)
                .filePath(filePath)
                .fileType(fileType)
                .fileSize(fileSize)
                .cloudYn(cloudYn)
                .register("M000000002")
                .updater("M000000002")
                .build();
    }
}
