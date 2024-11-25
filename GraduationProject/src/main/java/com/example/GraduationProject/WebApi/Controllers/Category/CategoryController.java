package com.example.GraduationProject.WebApi.Controllers.Category;

import com.example.GraduationProject.Common.Entities.Category;
import com.example.GraduationProject.Core.Services.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @PostMapping
    public ResponseEntity<Category> addCategory(@RequestBody Category category) {
        Category savedCategory = categoryService.addCategory(category);
        return ResponseEntity.ok(savedCategory);
    }

    @PutMapping("/{categoryName}")
    public ResponseEntity<Category> updateCategory(
            @PathVariable String categoryName,
            @RequestBody Category categoryDetails) {
        Category updatedCategory = categoryService.updateCategory(categoryName, categoryDetails);
        return ResponseEntity.ok(updatedCategory);
    }

    @DeleteMapping("/{categoryName}")
    public ResponseEntity<String> deleteCategory(@PathVariable String categoryName) {
        categoryService.deleteCategory(categoryName);
        return ResponseEntity.ok("Category deleted successfully");
    }

    @GetMapping
    public ResponseEntity<List<Category>> getAllCategories() {
        List<Category> categories = categoryService.getAllCategories();
        return ResponseEntity.ok(categories);
    }

    @GetMapping("/{categoryName}")
    public ResponseEntity<Category> getCategoryByName(@PathVariable String categoryName) {
        return categoryService.getCategoryByName(categoryName)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
