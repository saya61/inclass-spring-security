package ac.su.inclassspringsecurity.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.stereotype.Service;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class OrderProductServiceTest {

    @Autowired
    private OrderProductService orderProductService;
    @Test
    void createDummyOrderProduct() {
        orderProductService.createDummyOrderProduct();
    }
}