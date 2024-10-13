package ru.practicum.ewm.category.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.ewm.category.model.Category;
import ru.practicum.ewm.user.model.User;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

}