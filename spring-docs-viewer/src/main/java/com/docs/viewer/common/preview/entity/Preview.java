package com.docs.viewer.common.preview.entity;

import com.docs.viewer.common.file.entity.File;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "PREVIEW", // 전시배너상세
        indexes={
                @Index(name="FK_CMM_FILE_TO_PREVIEW", columnList="FILE_CD")
        }
)
@Getter @Setter
@EqualsAndHashCode(of = "previewId")
@Builder @NoArgsConstructor @AllArgsConstructor
public class Preview {

    @Id
    @Column(name = "PREVIEW_ID", columnDefinition = "BIGINT(20) COMMENT '파일프리뷰관리번호'")
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 기본 키 생성을 데이터베이스에 위임 (AUTO_INCREMENT)
    private Long previewId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "FILE_CD",
            nullable = false,
            updatable = false,
            foreignKey = @ForeignKey(name = "FK_CMM_FILE_TO_PREVIEW"),
            columnDefinition = "BIGINT(20) COMMENT '파일코드'"
    )
    private File file;

    @Column(name = "FILE_PATH", columnDefinition = "VARCHAR(200) COMMENT '파일경로'")
    private String filePath;

    @Column(name = "FILE_TYPE", columnDefinition = "VARCHAR(100) COMMENT '파일유형'")
    private String fileType;

    @Column(name = "FILE_SIZE", columnDefinition = "INT(11) COMMENT '파일크기'")
    private Integer fileSize;

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

    public static Preview of(File fileInfo, String uploadPath, String mimeType, long fileSize) {
        return Preview.builder()
                .file(fileInfo)
                .filePath(uploadPath)
                .fileType(mimeType)
                .fileSize(Long.valueOf(fileSize).intValue())
                .register("M000000002")
                .updater("M000000002")
                .build();
    }
}
