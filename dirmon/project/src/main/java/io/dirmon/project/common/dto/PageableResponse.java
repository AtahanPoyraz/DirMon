package io.dirmon.project.common.dto;

import lombok.*;
import org.springframework.data.domain.Page;

import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PageableResponse<T> {
    private T content;

    private PageableInfo pageable;

    @Setter
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PageableInfo {
        private int page;

        private int size;

        private long totalItems;

        private long totalPages;

        private boolean hasNext;

        private boolean hasPrevious;

        private boolean isFirst;

        private boolean isLast;

        private SortInfo sort;
    }

    @Setter
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SortInfo {
        private boolean sorted;

        private boolean unsorted;

        private boolean empty;
    }

    public static <T> PageableResponse<List<T>> fromPage(Page<@NonNull T> page) {
        List<T> content = page.getContent();

        PageableResponse<List<T>> pageableResponse = new PageableResponse<>();
        pageableResponse.setContent(content);

        PageableInfo pageableInfo = new PageableInfo();
        pageableInfo.setPage(page.getNumber());
        pageableInfo.setSize(page.getSize());
        pageableInfo.setTotalItems(page.getTotalElements());
        pageableInfo.setTotalPages(page.getTotalPages());

        pageableInfo.setHasNext(page.hasNext());
        pageableInfo.setHasPrevious(page.hasPrevious());
        pageableInfo.setFirst(page.isFirst());
        pageableInfo.setLast(page.isLast());

        SortInfo sortInfo = new SortInfo();
        sortInfo.setSorted(page.getSort().isSorted());
        sortInfo.setUnsorted(page.getSort().isUnsorted());
        sortInfo.setEmpty(page.getSort().isEmpty());

        pageableInfo.setSort(sortInfo);

        pageableResponse.setPageable(pageableInfo);

        return pageableResponse;
    }
}
