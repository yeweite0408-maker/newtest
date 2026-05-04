package com.mall.service;

import com.mall.entity.Product;
import com.mall.mapper.ProductMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {

    private final ProductMapper productMapper;

    public ProductService(ProductMapper productMapper) {
        this.productMapper = productMapper;
    }

    public List<Product> findAll() {
        return productMapper.findAll();
    }

    public Product findById(Long id) {
        return productMapper.findById(id);
    }

    public List<Product> search(String keyword) {
        return productMapper.search(keyword);
    }

    public boolean add(Product product) {
        return productMapper.insert(product) > 0;
    }

    public boolean update(Product product) {
        return productMapper.update(product) > 0;
    }

    public boolean delete(Long id) {
        return productMapper.deleteById(id) > 0;
    }
}
