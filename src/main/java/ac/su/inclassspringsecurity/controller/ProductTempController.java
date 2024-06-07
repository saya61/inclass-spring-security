package ac.su.inclassspringsecurity.controller;

import ac.su.inclassspringsecurity.domain.Product;
import ac.su.inclassspringsecurity.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
@RequestMapping("/products-temp")
public class ProductTempController {
    private final ProductService productService;

    @GetMapping("/thymeleaf/ex01")
    public String ex01(Model model) {
        Product product = new Product();
        product.setName("this is sample");
        model.addAttribute("data", "This is assigned from controller!!");
        model.addAttribute("data1", "Change2!");
        model.addAttribute("data2", "Change3!");
        model.addAttribute("data3", "Change4!");
        model.addAttribute("data4", "Change5!");
        // toString 형태 why? html 에서 th:text 바뀌는 과정.
        model.addAttribute("data5", product);
        return "thymeleaf/ex01";
    }

    @GetMapping("/thymeleaf/products-page")
    public String getProductPage(
            @RequestParam(value = "page", required = false, defaultValue = "0") int page,
            @RequestParam(value = "size", required = false, defaultValue = "15") int size,
            Model model
    ) {
//        List<Product> products = productService.getProductPage(page, size);
        List<Product> products = productService.getValidProductList(page, size);    // DELETE 를 넘어오지도 않게
        model.addAttribute("products", products);
        return "thymeleaf/products-page";
    }

    @GetMapping("/products-layout-applied")
    public String productLayoutList(
            @RequestParam(value = "page", required = false, defaultValue = "0") int page,
            @RequestParam(value = "size", required = false, defaultValue = "15") int size,
            Model model
    ) {
        List<Product> products = productService.getValidProductList(page,size);
        model.addAttribute("products", products);
        return "products-layout-applied";
    }

    @GetMapping("/products-pagenav")
    public String productPageNav(
            @RequestParam(value = "page", required = false, defaultValue = "0") int page,
            @RequestParam(value = "size", required = false, defaultValue = "5") int size,
            Model model
    ) {
        Page<Product> productsPage = productService.getValidProductPage(page,size);
        model.addAttribute("productsPage", productsPage);
        model.addAttribute("pageNum", page);
        model.addAttribute("pageSize", size);
        return "products-pagenav";
    }

    @GetMapping("/product-detail")
    public String productDetail(
            Model model
    ) {
        List<Product> productsPage = productService.getValidProduct();
        model.addAttribute("productsPage", productsPage);
        return "product-detail";
    }

    @GetMapping("/{id}")
    public String getProductById(@PathVariable("id") long id, Model model) {
        Optional<Product> product = productService.getProductById(id);
        if (product.isPresent()) {
            model.addAttribute("product", product.get());
            return "product-form"; // 템플릿 파일 이름
        } else {
            return "redirect:/product-temp/products-pagenav"; // 못 찾을 경우
        }
    }

    @PostMapping("/{id}")    // Put 맵핑은 Form으로 하기는 조금 그렇다.
    public String saveProduct(@ModelAttribute Product product) {
        product = productService.saveProductUpsert(product);
        return "redirect:/products-temp/" + product.getId(); // 업데이트 후 리디렉션 할 URL
    }
}
