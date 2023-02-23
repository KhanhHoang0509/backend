package com.shoppingcart.admin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication //tự động cấu hình Spring, và tự động quét (Scan) toàn bộ project để tìm ra các thành phần Spring (Controller, Bean, Service,...)
@ComponentScan(basePackages = { "com.shoppingcart.admin.*", "com.shoppingcart.admin" }) // quét package
@EnableJpaRepositories(basePackages = { "com.shoppingcart.admin.*" }) // quét package để xài UserRepository
@EntityScan({ "com.shoppingcart.admin.*" }) // dựa vào cấu hình của các package khác có khai báo @Entity sẽ tạo table
											// tương ứng
public class BackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(BackendApplication.class, args);
	}

}
