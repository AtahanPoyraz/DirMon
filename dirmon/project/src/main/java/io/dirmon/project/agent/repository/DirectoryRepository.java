package io.dirmon.project.agent.repository;

import io.dirmon.project.agent.model.DirectoryModel;
import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface DirectoryRepository extends JpaRepository<@NonNull DirectoryModel, @NonNull UUID> {
}
