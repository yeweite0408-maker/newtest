package com.mall.mapper;

import com.mall.entity.Product;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface ProductMapper {

    @Select("SELECT * FROM product")
    List<Product> findAll();

    @Select("SELECT * FROM product WHERE id = #{id}")
    Product findById(Long id);

    @Select("SELECT * FROM product WHERE name LIKE CONCAT('%', #{keyword}, '%') OR description LIKE CONCAT('%', #{keyword}, '%')")
    List<Product> search(String keyword);

    @Insert("INSERT INTO product(name, description, price, image, category, stock, developer) VALUES (#{name}, #{description}, #{price}, #{image}, #{category}, #{stock}, #{developer})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Product product);

    @Update("UPDATE product SET name=#{name}, description=#{description}, price=#{price}, image=#{image}, category=#{category}, stock=#{stock}, developer=#{developer} WHERE id=#{id}")
    int update(Product product);

    @Delete("DELETE FROM product WHERE id = #{id}")
    int deleteById(Long id);
}
