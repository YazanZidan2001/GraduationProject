package com.example.GraduationProject.Core.Services;

import com.example.GraduationProject.Common.Entities.Category;
import com.example.GraduationProject.Core.Repositories.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    @Transactional
    public Category addCategory(Category category) {
        return categoryRepository.save(category);
    }

    @Transactional
    public Category updateCategory(String categoryName, Category categoryDetails) {
        Category category = categoryRepository.findById(categoryName)
                .orElseThrow(() -> new IllegalArgumentException("Category not found with name: " + categoryName));
        category.setCategory_name(categoryDetails.getCategory_name());
        return categoryRepository.save(category);
    }

    @Transactional
    public void deleteCategory(String categoryName) {
        if (!categoryRepository.existsById(categoryName)) {
            throw new IllegalArgumentException("Category not found with name: " + categoryName);
        }
        categoryRepository.deleteById(categoryName);
    }

    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    public Optional<Category> getCategoryByName(String categoryName) {
        return categoryRepository.findById(categoryName);
    }

    /**
     * Get all categories, optionally filtered by a search string.
     */
    public List<Category> getAllCategories(String search) {
        return categoryRepository.searchCategories(search);
    }
}
