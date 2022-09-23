package com.devillage.teamproject.service.post;

import com.devillage.teamproject.entity.*;
import com.devillage.teamproject.exception.BusinessLogicException;
import com.devillage.teamproject.exception.ExceptionCode;
import com.devillage.teamproject.repository.post.BookmarkRepository;
import com.devillage.teamproject.repository.post.LikeRepository;
import com.devillage.teamproject.repository.post.PostRepository;
import com.devillage.teamproject.repository.post.ReportedPostRepository;
import com.devillage.teamproject.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final BookmarkRepository bookmarkRepository;
    private final ReportedPostRepository reportedPostRepository;
    private final LikeRepository likeRepository;

    @Override
    public Post savePost(Post post) {
        Post savedPost = postRepository.save(post);
        return savedPost;
    }

    @Override
    public Post editPost() {
        return null;
    }

    @Override
    public Post getPost() {
        return null;
    }

    @Override
    public List<Post> getPosts() {
        return null;
    }

    @Override
    public void deletePost() {

    }

    @Override
    public Bookmark postBookmark(Long postId) {
        Long userId = 1L; // Security 메서드 구현 필요
        User user = verifyUser(userId);
        Post post = verifyPost(postId);

        List<Bookmark> findBookmark = bookmarkRepository.findByUserIdAndPostId(userId, postId);

        if (!findBookmark.isEmpty()) {
            Bookmark bookmark = findBookmark.get(0);
            bookmarkRepository.delete(bookmark);
            return bookmark;
        }

        Bookmark bookmark = new Bookmark(user, post);
        user.addBookmark(bookmark);
        return bookmark;
    }

    @Override
    public ReportedPost postReport(Long postId) {
        Long userId = 1L; // Security 메서드 구현 필요
        User user = verifyUser(userId);
        Post post = verifyPost(postId);

        List<ReportedPost> findReport = reportedPostRepository.findByUserIdAndPostId(userId, postId);

        if (!findReport.isEmpty()) {
            throw new BusinessLogicException(ExceptionCode.ALREADY_REPORTED);
        }

        ReportedPost reportedPost = new ReportedPost(user, post);
        post.addReportedPosts(reportedPost);
        return reportedPost;
    }

    @Override
    public Like postLike(Long postId) {
        Long userId = 1L; // Security 메서드 구현 필요
        User user = verifyUser(userId);
        Post post = verifyPost(postId);

        List<Like> findLike = likeRepository.findByUserIdAndPostId(userId, postId);

        if (!findLike.isEmpty()) {
            Like like = findLike.get(0);
            likeRepository.delete(like);
            return like;
        }


        Like like = new Like(user, post);
        user.addLike(like);
        return like;
    }

    private Post verifyPost(Long postId) {
        Optional<Post> findPost = postRepository.findById(postId);

        return findPost.orElseThrow(
                () -> new BusinessLogicException(ExceptionCode.POST_NOT_FOUND)
        );
    }

    private User verifyUser(Long userId) {
        Optional<User> findUser = userRepository.findById(userId);

        return findUser.orElseThrow(
                () -> new BusinessLogicException(ExceptionCode.USER_NOT_FOUND)
        );
    }
}