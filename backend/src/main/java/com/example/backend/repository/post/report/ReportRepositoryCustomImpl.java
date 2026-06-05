package com.example.backend.repository.post.report;

import com.example.backend.entity.QPosts;
import com.example.backend.entity.QReport;
import com.example.backend.entity.QUsers;
import com.example.backend.entity.Report;
import com.example.backend.enums.ReportStatus;
import com.example.backend.repository.post.report.ReportRepositoryCustom;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;

@RequiredArgsConstructor
public class ReportRepositoryCustomImpl implements ReportRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<Report> findReportsByStatusWithDetails(ReportStatus status, Pageable pageable) {
        QReport report = QReport.report;
        QPosts post = QPosts.posts;
        QUsers postUser = new QUsers("postUser");
        QUsers reporter = new QUsers("reporter");

        JPAQuery<Report> query = queryFactory.selectFrom(report)
                .leftJoin(report.post, post).fetchJoin()
                .leftJoin(post.user, postUser).fetchJoin()
                .leftJoin(report.reporter, reporter).fetchJoin()
                .where(report.status.eq(status));

        long total = query.fetchCount();

        List<Report> content = query
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                // Basic sorting support could be added here if needed, but for now we rely on the service
                // If service passes Sort, we might need to apply it. I'll just apply order by createdDate desc manually as it's the default.
                .orderBy(report.createdDate.desc())
                .fetch();

        return new PageImpl<>(content, pageable, total);
    }

    @Override
    public Page<Report> findAllReportsWithDetails(Pageable pageable) {
        QReport report = QReport.report;
        QPosts post = QPosts.posts;
        QUsers postUser = new QUsers("postUser");
        QUsers reporter = new QUsers("reporter");

        JPAQuery<Report> query = queryFactory.selectFrom(report)
                .leftJoin(report.post, post).fetchJoin()
                .leftJoin(post.user, postUser).fetchJoin()
                .leftJoin(report.reporter, reporter).fetchJoin();

        long total = query.fetchCount();

        List<Report> content = query
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(report.createdDate.desc())
                .fetch();

        return new PageImpl<>(content, pageable, total);
    }

    @Override
    public List<Report> findReportsByPostIdWithDetails(String postId) {
        QReport report = QReport.report;
        QPosts post = QPosts.posts;
        QUsers postUser = new QUsers("postUser");
        QUsers reporter = new QUsers("reporter");

        return queryFactory.selectFrom(report)
                .leftJoin(report.post, post).fetchJoin()
                .leftJoin(post.user, postUser).fetchJoin()
                .leftJoin(report.reporter, reporter).fetchJoin()
                .where(report.post.id.eq(postId))
                .orderBy(report.createdDate.desc())
                .fetch();
    }
}
