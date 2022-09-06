package com.quadrant.blog.service;

import com.quadrant.blog.dto.BaseDataResponse;
import com.quadrant.blog.dto.PageDataResponse;
import com.quadrant.blog.dto.blog.BlogDataRequest;
import com.quadrant.blog.dto.blog.BlogDataResponse;
import com.quadrant.blog.entity.BlogEntity;
import com.quadrant.blog.exception.ResourceNotFoundException;
import com.quadrant.blog.repository.BlogRepository;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.core.env.Environment;

import javax.transaction.Transactional;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class BlogService {

    private final BlogRepository blogRepository;
    private final Path fileStorageLocation;
    private final ModelMapper modelMapper;
    private final Log logger = LogFactory.getLog(getClass());
    private final Environment environment;

    public BlogService(ModelMapper modelMapper,
                       BlogRepository blogRepository,
                       Environment environment) {
        this.environment = environment;
        this.modelMapper = modelMapper;
        this.blogRepository = blogRepository;
        this.fileStorageLocation = Paths.get(environment.getProperty("app.file.upload")).toAbsolutePath().normalize();

        try {
            Files.createDirectories(fileStorageLocation);
        } catch (Exception ex) {
            throw new RuntimeException("Could not create the directory where the uploaded files will be stored.", ex);
        }
    }

    public BaseDataResponse<List<BlogDataResponse>> getBlogs() {

        BaseDataResponse<List<BlogDataResponse>> response =  new BaseDataResponse<>();

        List<BlogEntity> findBlogsAll = blogRepository.findAll();

        List<BlogDataResponse> blogs = findBlogsAll.stream()
                .map(blog -> modelMapper.map(blog, BlogDataResponse.class))
                .collect(Collectors.toList());

        response.setStatus("SUCCESS");
        response.setCode(HttpStatus.OK);
        response.setPayload(blogs);

        return response;
    }

    public BaseDataResponse<PageDataResponse<BlogDataResponse>> getBlogsWithPaging(String search, Pageable pageable) {

        BaseDataResponse<PageDataResponse<BlogDataResponse>> response =  new BaseDataResponse<>();

        Page<BlogEntity> findBlogsByTitleOrDescriptionContains = blogRepository.findByTitleOrDescriptionContains(search, pageable);

        List<BlogDataResponse> blogs = findBlogsByTitleOrDescriptionContains.stream()
                .map(blog -> modelMapper.map(blog, BlogDataResponse.class))
                .collect(Collectors.toList());

        PageDataResponse<BlogDataResponse> pageDataResponse = new PageDataResponse<>();

        pageDataResponse.setData(blogs);
        pageDataResponse.setPageable(findBlogsByTitleOrDescriptionContains.getPageable());

        response.setStatus("SUCCESS");
        response.setCode(HttpStatus.OK);
        response.setPayload(pageDataResponse);

        return response;
    }

    public BaseDataResponse<BlogDataResponse> getBlog(Long id) {

        BaseDataResponse<BlogDataResponse> response =  new BaseDataResponse<>();

        Optional<BlogEntity> findBlogById = blogRepository.findById(id);

        if (findBlogById.isPresent()) {
            BlogDataResponse blog = modelMapper.map(findBlogById, BlogDataResponse.class);

            response.setStatus("SUCCESS");
            response.setCode(HttpStatus.OK);
            response.setPayload(blog);
        } else {
            logger.info("Category not found");

            response.setStatus("ERROR");
            response.setCode(HttpStatus.NOT_FOUND);
            response.setPayload(null);
        }

        return response;
    }

    public BaseDataResponse<BlogDataResponse> createBlog(BlogEntity blogEntity, MultipartFile mf, BaseDataResponse<BlogDataResponse> response) {

        if (!mf.isEmpty()) {
            String fileName = mf.getOriginalFilename();

            if (fileName.contains("..")) {
                throw new RuntimeException("Filename contains invalid path sequence " + fileName);
            }

            String randomID = UUID.randomUUID().toString();
            String fileRenamed = randomID.concat(fileName.substring(fileName.lastIndexOf(".")));

            try {
                Path targetLocation = this.fileStorageLocation.resolve(fileRenamed);
                Files.copy(mf.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

                blogEntity.setImageFile(fileRenamed);
                blogEntity.setImagePath(environment.getProperty("app.file.upload")
                        .replace(".", ""));
                blogEntity.setImageAbsolute(environment.getProperty("app.file.upload.mapping")
                        .replace("file://", ""));
                blogEntity.setImageSize(mf.getSize());
            } catch (IOException ex) {
                throw new RuntimeException("Could not store file " + fileName + ". Please try again!", ex);
            }
        }

        BlogEntity saveBlog = blogRepository.save(blogEntity);

        BlogDataResponse blogResponse = modelMapper.map(saveBlog, BlogDataResponse.class);

        response.setStatus("SUCCESS");
        response.setCode(HttpStatus.OK);
        response.setPayload(blogResponse);

        return response;
    }

    public BaseDataResponse<BlogDataResponse> updateBlog(Long id, BlogDataRequest request, BaseDataResponse<BlogDataResponse> response) {

        Optional<BlogEntity> findByIdBlog = blogRepository.findById(id);

        if (findByIdBlog.isPresent()) {
            MultipartFile mf = request.getImage();

            findByIdBlog.get().setTitle(request.getTitle());
            findByIdBlog.get().setSlug(request.getSlug());
            findByIdBlog.get().setDescription(request.getDescription());

            if (mf != null) {
                String fileName = mf.getOriginalFilename();

                if (fileName.contains("..")) {
                    throw new RuntimeException("Filename contains invalid path sequence " + fileName);
                }

                String randomID = UUID.randomUUID().toString();
                String fileRenamed = randomID.concat(fileName.substring(fileName.lastIndexOf(".")));

                try {
                    Path targetLocation = this.fileStorageLocation.resolve(fileRenamed);
                    Files.copy(mf.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

                    File fileOld = new File(findByIdBlog.get().getImageAbsolute() + File.separator + findByIdBlog.get().getImageFile());
                    fileOld.delete();

                    findByIdBlog.get().setImageFile(fileRenamed);
                    findByIdBlog.get().setImagePath(environment.getProperty("app.file.upload")
                            .replace(".", ""));
                    findByIdBlog.get().setImageAbsolute(environment.getProperty("app.file.upload.mapping")
                            .replace("file://", ""));
                    findByIdBlog.get().setImageSize(mf.getSize());
                } catch (IOException ex) {
                    throw new RuntimeException("Could not store file " + fileName + ". Please try again!", ex);
                }
            }

            BlogEntity blogCategory = blogRepository.save(findByIdBlog.get());

            BlogDataResponse blogResponse = modelMapper.map(blogCategory, BlogDataResponse.class);

            response.setStatus("SUCCESS");
            response.setCode(HttpStatus.OK);
            response.setPayload(blogResponse);
        } else {
            response.setStatus("ERROR");
            response.setCode(HttpStatus.NOT_FOUND);
            response.setPayload(null);
        }

        return response;
    }

    public BaseDataResponse<BlogDataResponse> updateBlogView(Long id, BaseDataResponse<BlogDataResponse> response) {

        Optional<BlogEntity> findByIdBlog = blogRepository.findById(id);

        if (findByIdBlog.isPresent()) {
            findByIdBlog.get().setViewed(findByIdBlog.get().getViewed() + 1);

            BlogEntity updateBlog =  blogRepository.save(findByIdBlog.get());

            BlogDataResponse blogResponse = modelMapper.map(updateBlog, BlogDataResponse.class);

            logger.info("Blog view updated");

            response.setStatus("SUCCESS");
            response.setCode(HttpStatus.OK);
            response.setPayload(blogResponse);
        } else {
            logger.info("Blog view not found");

            response.setStatus("ERROR");
            response.setCode(HttpStatus.NOT_FOUND);
            response.setPayload(null);
        }

        return response;
    }

    public void deleteBlog(Long id) {
        BlogEntity finByIdResponse = blogRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException("Blog", "Id", id));

        File fileOld = new File(finByIdResponse.getImageAbsolute() + File.separator + finByIdResponse.getImageFile());
        fileOld.delete();

        blogRepository.deleteById(id);
    }
}
