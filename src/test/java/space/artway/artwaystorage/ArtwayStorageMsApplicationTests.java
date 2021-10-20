package space.artway.artwaystorage;

import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cloud.contract.verifier.messaging.boot.AutoConfigureMessageVerifier;
import org.springframework.context.annotation.Profile;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.test.web.servlet.setup.StandaloneMockMvcBuilder;
import org.springframework.web.multipart.MultipartFile;
import space.artway.artwaystorage.controller.ContentController;
import space.artway.artwaystorage.service.DeleteService;
import space.artway.artwaystorage.service.SaveContentService;
import space.artway.artwaystorage.service.dto.FileDto;

import static org.mockito.ArgumentMatchers.any;

@Profile("dev")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@DirtiesContext
@AutoConfigureMessageVerifier
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ArtwayStorageMsApplicationTests {

    @Autowired
    ContentController contentController;

    @MockBean
    SaveContentService saveContentService;

    @MockBean
    DeleteService deleteService;

    @BeforeAll
    void setUp() {
        Mockito.when(saveContentService.saveContent(any(MultipartFile.class))).thenReturn(new FileDto("jdkjs-djskjd-dksjd-sjdk-sjs8sk", "andrew"));

        StandaloneMockMvcBuilder standaloneMockMvcBuilder = MockMvcBuilders.standaloneSetup(contentController);
        RestAssuredMockMvc.standaloneSetup(standaloneMockMvcBuilder);

    }

    @Test
    void contextLoads() {
    }

}
