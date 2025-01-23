package com.example.GraduationProject.WebApi.Controllers.Admin.Category;

import com.example.GraduationProject.Common.Entities.Category;
import com.example.GraduationProject.Common.Entities.User;
import com.example.GraduationProject.Core.Services.AuthenticationService;
import com.example.GraduationProject.Core.Services.CategoryService;
import com.example.GraduationProject.SessionManagement;
import com.example.GraduationProject.Common.Responses.GeneralResponse;
import com.example.GraduationProject.WebApi.Exceptions.UserNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/categories")
@RequiredArgsConstructor
public class CategoryController extends SessionManagement {

    private final CategoryService categoryService;
    private final AuthenticationService service;

    @PostMapping("/")
    public ResponseEntity<GeneralResponse> addCategory(@RequestBody @Valid Category category, HttpServletRequest httpServletRequest) throws UserNotFoundException {
        String token = service.extractToken(httpServletRequest);
        User user = service.extractUserFromToken(token);
        validateLoggedInAdmin(user);
        categoryService.addCategory(category);
        return ResponseEntity.ok(GeneralResponse.builder().message("Category added successfully").build());
    }

    @PutMapping("/")
    public ResponseEntity<GeneralResponse> updateCategory(@RequestBody @Valid Category categoryDetails, HttpServletRequest httpServletRequest) throws UserNotFoundException {
        String token = service.extractToken(httpServletRequest);
        User user = service.extractUserFromToken(token);
        validateLoggedInAdmin(user);
        categoryService.updateCategory(categoryDetails.getCategory_name(), categoryDetails);
        return ResponseEntity.ok(GeneralResponse.builder().message("Category updated successfully").build());
    }

    @DeleteMapping("/{categoryName}")
    public ResponseEntity<GeneralResponse> deleteCategory(@PathVariable String categoryName, HttpServletRequest httpServletRequest) throws UserNotFoundException {
        String token = service.extractToken(httpServletRequest);
        User user = service.extractUserFromToken(token);
        validateLoggedInAdmin(user);
        categoryService.deleteCategory(categoryName);
        return ResponseEntity.ok(GeneralResponse.builder().message("Category deleted successfully").build());
    }

    @GetMapping("/{categoryName}")
    public ResponseEntity<Category> getCategoryByName(@PathVariable String categoryName, HttpServletRequest httpServletRequest) throws UserNotFoundException {
        String token = service.extractToken(httpServletRequest);
        User user = service.extractUserFromToken(token);
        validateLoggedInAdmin(user);
        return categoryService.getCategoryByName(categoryName)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("")
    public ResponseEntity<List<Category>> getAllCategories(HttpServletRequest httpServletRequest) throws UserNotFoundException {
        String token = service.extractToken(httpServletRequest);
        User user = service.extractUserFromToken(token);
        validateLoggedInAdmin(user);
        List<Category> categories = categoryService.getAllCategories();
        return ResponseEntity.ok(categories);
    }


    /**
     * GET /categories?search=xxx
     * Returns all categories, or filters by search string on category_name.
     */
    @GetMapping("/get-all-categories")
    public ResponseEntity<?> getAllCategories(@RequestParam(required = false) String search,
                                              HttpServletRequest httpServletRequest)
            throws UserNotFoundException {

        String token = service.extractToken(httpServletRequest);
        User user = service.extractUserFromToken(token);
        validateLoggedInDoctorOrAdmin(user);

        try {
            List<Category> categories = categoryService.getAllCategories(search);
            if (categories.isEmpty()) {
                return ResponseEntity.ok("No categories found.");
            }
            return ResponseEntity.ok(categories);
        } catch (Exception ex) {
            return ResponseEntity.internalServerError()
                    .body("Error fetching categories: " + ex.getMessage());
        }
    }
}
