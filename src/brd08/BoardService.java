package brd08;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BoardService {
    BoardDAO boardDAO;

    public BoardService(){
        boardDAO = new BoardDAO();
    }

    public Map listArticles(Map<String, Integer> pagingMap) {
        Map articlesMap = new HashMap();
        List<ArticleVO> articlesList = boardDAO.selectAllArticles(pagingMap);
        int totArticles = boardDAO.selectTotArticles();
        articlesMap.put("articlesList",articlesList);
        articlesMap.put("totArticles",totArticles);

        return articlesMap;
    }

    public int addArticle(ArticleVO article) {
        return boardDAO.insertNewArticle(article);
    }
}
