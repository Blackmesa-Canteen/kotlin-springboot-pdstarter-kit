package org.worker996.kotlinsprintbootpd.article

import jakarta.persistence.EntityManager
import jakarta.persistence.PersistenceContext
import jakarta.persistence.criteria.*
import org.springframework.stereotype.Repository
import org.worker996.kotlinsprintbootpd.tag.TagEntity
import org.worker996.kotlinsprintbootpd.user.UserEntity

@Repository
class CustomArticleRepositoryImpl: CustomArticleRepository {

    @PersistenceContext
    private lateinit var entityManager: EntityManager

    private val logger = org.slf4j.LoggerFactory.getLogger(CustomArticleRepositoryImpl::class.java)

    /**
     * This method counts the number of articles based on the provided filters.
     *
     * @param tag The tag to filter articles by. Can be null.
     * @param author The author username to filter articles by. Can be null.
     * @param favorited The username of the user who favorited the articles to filter by. Can be null.
     *
     * @return The number of articles that match the provided filters.
     */
    override fun countArticles(tag: String?, author: String?, favorited: String?): Long {
        val cb: CriteriaBuilder = entityManager.criteriaBuilder
        val cq: CriteriaQuery<Long> = cb.createQuery(Long::class.java)
        val root: Root<ArticleEntity> = cq.from(ArticleEntity::class.java)

        cq.select(cb.count(root))

        val predicates = mutableListOf<Predicate>()

        if (!tag.isNullOrBlank()) {
            val tagsJoin = root.join<ArticleEntity, TagEntity>("tags", JoinType.LEFT)
            predicates.add(cb.equal(tagsJoin.get<String>("name"), tag))
        }

        if (!author.isNullOrBlank()) {
            val authorJoin = root.join<ArticleEntity, UserEntity>("author", JoinType.LEFT)
            predicates.add(cb.equal(authorJoin.get<String>("username"), author))
        }

        if (!favorited.isNullOrBlank()) {
            val favoritedJoin = root.join<ArticleEntity, UserEntity>("favoritedBy", JoinType.LEFT)
            predicates.add(cb.equal(favoritedJoin.get<String>("username"), favorited))
        }

        if (predicates.isNotEmpty()) {
            cq.where(*predicates.toTypedArray())
        }

        val query = entityManager.createQuery(cq)

        val hibernateQuery = query.unwrap(org.hibernate.query.Query::class.java)
        logger.debug("Generated Query: ${hibernateQuery.queryString.toString()}")
        logger.debug("Parameters: tag=$tag, author=$author, favorited=$favorited")

        return query.singleResult
    }
}