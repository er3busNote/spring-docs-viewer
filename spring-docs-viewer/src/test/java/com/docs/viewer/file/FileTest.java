package com.docs.viewer.file;

import com.docs.viewer.common.BaseTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class FileTest extends BaseTest {

    @Test
    @DisplayName("공통 이미지정보 저장 확인")
    public void createFileTest() throws Exception {

        MockMultipartFile file1 = new MockMultipartFile("files", "file1.txt", "image/jpeg", "Test file content 1".getBytes());
        MockMultipartFile file2 = new MockMultipartFile("files", "file2.txt", "image/png", "Test file content 2".getBytes());

        mockMvc.perform(multipart("/file/upload")
                        .file(file1)
                        .file(file2)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andDo(print())
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("공통 이미지정보 다운로드 확인")
    public void findFileImageTest() throws Exception {

        String fileAttachId = String.valueOf(100016);

        mockMvc.perform(get("/file")
                        .param("id", fileAttachId))
                .andDo(print())
                .andExpect(status().isOk());
    }
}
