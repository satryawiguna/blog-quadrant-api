package com.quadrant.blog.dto;

import com.quadrant.blog.dto.category.CategoryDataResponse;
import org.springframework.data.domain.Pageable;

import java.util.List;

public class PageDataResponse<T> {

    private List<T> data;
    private Pageable pageable;


    public List<T> getData() {
        return data;
    }

    public void setData(List<T> data) {
        this.data = data;
    }

    public Pageable getPageable() {
        return pageable;
    }

    public void setPageable(Pageable pageable) {
        this.pageable = pageable;
    }
}
