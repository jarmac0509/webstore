package com.packt.webstore.controller;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.MatrixVariable;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.packt.webstore.domain.Product;
import com.packt.webstore.service.ProductService;
@Controller
@RequestMapping("/products")
public class ProductController {
	@Autowired 
	private ProductService productService;
	@RequestMapping
	public String list(Model model) {
	model.addAttribute("products", productService.getAllProducts());
	return "products";
	}
	@RequestMapping("/all")
	public String allProducts(Model model) {
	model.addAttribute("products", productService.getAllProducts());
	return "products";
	}
	@RequestMapping("/{category}")
	public String getProductsByCategory(Model model, @PathVariable("category") String productCategory) {
	model.addAttribute("products", productService.getProductsByCategory(productCategory));
	return "products";
	}
//	http://localhost:8080/webstore/products/filter/ByCriteria;brand=google,dell;category=tablet,laptop
	@RequestMapping("/filter/{ByCriteria}")
	public String getProductsByFilter(@MatrixVariable(pathVar="ByCriteria") Map<String,List<String>> filterParams, Model model) {
	model.addAttribute("products", productService.getProductsByFilter(filterParams));
	return "products";
	}
	@RequestMapping("/product")
	public String getProductById(@RequestParam("id") String productId, Model model) {
	model.addAttribute("product", productService.getProductById(productId));
	return "product";
	}
//   http://localhost:8080/webstore/products/tablet/price;low=200;high=400?manufacturer=Google
	@RequestMapping("/{category}/{price}")
	public String filterProducts(Model model,@PathVariable("category") String productCategory,@MatrixVariable(pathVar="price") Map<String,Integer> filterParams,@RequestParam("manufacturer") String manufacturer ){
		Set<Product> filteredProducts=new HashSet<Product>();
		filteredProducts.addAll(productService.getProductsByCategory(productCategory));
		filteredProducts.addAll(productService.getProductsByManufacturer(manufacturer));
//		filteredProducts.addAll(productService.getProductsByPriceFilter(filterParams));
		model.addAttribute("products",filteredProducts);
		return "products";
	}
	@RequestMapping(value = "/add", method = RequestMethod.GET)
	public String getAddNewProductForm(Model model) {
	Product newProduct = new Product();
	model.addAttribute("newProduct", newProduct);
	return "addProduct";
	}
	@RequestMapping(value = "/add", method = RequestMethod.POST)
	public String processAddNewProductForm(@ModelAttribute("newProduct") Product newProduct,BindingResult result) {
		String[] suppressedFields = result.getSuppressedFields();
		if (suppressedFields.length > 0) {
		throw new RuntimeException("Pr�ba wi�zania niedozwolonych p�l:" + StringUtils.arrayToCommaDelimitedString(suppressedFields));
		}
		productService.addProduct(newProduct);
	return "redirect:/products";
	}
	@InitBinder
	public void initialiseBinder(WebDataBinder binder) {
	binder.setDisallowedFields("unitsInOrder", "discontinued");
	}
	
}