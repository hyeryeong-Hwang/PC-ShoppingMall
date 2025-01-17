package com.project.easyBuild.board.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.project.easyBuild.board.repository.AnnouncementRepository;
import com.project.easyBuild.board.entity.Announcement;

@Service
public class AnnouncementService {
    private final AnnouncementRepository announcementRepository;

    public AnnouncementService(AnnouncementRepository announcementRepository) {
        this.announcementRepository = announcementRepository;
    }


    private Sort getDefaultSort() {
        return Sort.by(Sort.Direction.DESC, "createdDate");
    }

    public Page<Announcement> findAll(Pageable pageable) {
        if (pageable.getSort().isUnsorted()) {
            pageable = PageRequest.of(
                    pageable.getPageNumber(),
                    pageable.getPageSize(),
                    getDefaultSort()
            );
        }
        return announcementRepository.findAll(pageable);
    }

    public List<Announcement> getAllAnnouncements() {
        return announcementRepository.findAll();
    }

    public List<Announcement> getAllAnnouncementsSortedByDate() {
        return announcementRepository.findAll(getDefaultSort());
    }

    public Announcement getAnnouncementById(Long id) {
        return announcementRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Announcement not found with ID: " + id));
    }

    public void saveAnnouncement(Announcement announcement) {
        if (announcement.getTitle() == null || announcement.getTitle().isEmpty()) {
            throw new IllegalArgumentException("Title cannot be null or empty");
        }
        if (announcement.getContent() == null || announcement.getContent().isEmpty()) {
            throw new IllegalArgumentException("Content cannot be null or empty");
        }

        if (announcement.getCreatedDate() == null) {
            announcement.setCreatedDate(LocalDateTime.now());
        }

        announcementRepository.save(announcement);
    }

    public void updateAnnouncement(Announcement announcement) {
        // 기존 엔티티 조회
        Announcement existingAnnouncement = announcementRepository.findById(announcement.getAnnouncId())
            .orElseThrow(() -> new NoSuchElementException("Announcement not found"));

        // 필요한 필드만 업데이트
        existingAnnouncement.setTitle(announcement.getTitle());
        existingAnnouncement.setContent(announcement.getContent());

        // 저장
        announcementRepository.save(existingAnnouncement);
    }

    public void deleteAnnouncement(Long id) {
        if (!announcementRepository.existsById(id)) {
            throw new NoSuchElementException("Cannot delete non-existing announcement with ID: " + id);
        }
        announcementRepository.deleteById(id);
    }

    public List<Announcement> getLatestAnnouncements(int limit) {
        return announcementRepository.findAll(
                PageRequest.of(0, limit, getDefaultSort())
        ).getContent();
    }
}
