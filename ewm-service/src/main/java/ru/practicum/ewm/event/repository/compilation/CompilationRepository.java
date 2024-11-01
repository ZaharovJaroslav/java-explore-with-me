package ru.practicum.ewm.event.repository.compilation;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import ru.practicum.ewm.event.model.Compilation;

public interface CompilationRepository extends JpaRepository<Compilation,Long>, JpaSpecificationExecutor<Compilation> {
}