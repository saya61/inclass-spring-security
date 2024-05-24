package ac.su.inclassspringsecurity.controller;

import ac.su.inclassspringsecurity.domain.Product;
import ac.su.inclassspringsecurity.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor    // final 으로 선언된 클래스 호출 할 때 여러 개의 생성자를 작성 안하게 도와줌
@RequestMapping("/products")
public class ProductRestController {
    private final ProductService productService;

    // 생성자 주입 방식 -> Annotation 으로 대체 가능
//    public ProductController(ProductService productService) {
//        this.productService = productService;
//    }

    @GetMapping
    public ResponseEntity<List<Product>> getAllProducts() {
        List<Product> products = productService.getAllProducts();
        return new ResponseEntity<>(products, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable("id") long id) {
        Optional<Product> product = productService.getProductById(id);
        return product.map(
                value -> new ResponseEntity<>(value, HttpStatus.OK)
        ).orElseGet(
                () -> new ResponseEntity<>(HttpStatus.NOT_FOUND)
        );
    }

    @GetMapping("/paginated")
    public ResponseEntity<List<Product>> getProductPage(    // param 미수신한 경우 기본값(0,15)
            @RequestParam(value = "page", required = false, defaultValue = "0") int page,
            @RequestParam(value = "size", required = false, defaultValue = "15") int size
    ) {
        List<Product> products = productService.getProductPage(page, size);
        return new ResponseEntity<>(products, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<Product> createProduct(
            // Product 객체를 수신 (JSON 타입 -> Product 객체로 자동 변환)
            @RequestBody Product product
    ) {
        // Service 에 직접적인 로직 위탁
        Product createdProduct = productService.createProduct(product);
        // 생성된 Product 객체를 반환 (JSON 타입으로 자동 변환)
        return new ResponseEntity<>(createdProduct, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Product> updateProductPut(
            @PathVariable("id") long id,
            @RequestBody Product product
    ) {
        // Upsert 동작으로 아래와 같이 구현
        Product updatedProduct = productService.updateProductPut(id, product);
        if (updatedProduct != null) {
            return new ResponseEntity<>(updatedProduct, HttpStatus.OK);
        }
        Product createProduct = productService.createProduct(product);
        return new ResponseEntity<>(createProduct, HttpStatus.CREATED);

        // return this.createProduct(product);
    }

//    @PatchMapping("/{id}")

    @DeleteMapping("{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable("id") long id) {
        boolean isDeleted = productService.deleteProduct(id);
        if (isDeleted) {
            return new ResponseEntity<>(null, HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
    }

    // 호출 시 파라미터로 수신한 count 만큼 더미 데이터 생성
    @GetMapping("/make-dummy")
    public ResponseEntity<List<Product>> makeDummyData(@RequestParam("count") int count) {
        // count 값이 1 이상 100 이하가 되도록 제약조건 추가하기!
        if (count < 1 || count > 100) {
            return new ResponseEntity<>(new ArrayList<>(), HttpStatus.BAD_REQUEST);
        }
        List<Product> products = productService.makeDummyData(count);
        return new ResponseEntity<>(products, HttpStatus.OK);
    }
}