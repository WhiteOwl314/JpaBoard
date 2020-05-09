package brd08;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class BoardDAO {
    private DataSource dataFactory;
    Connection conn;
    PreparedStatement pstmt;

    public BoardDAO(){
        try{
            Context ctx = new InitialContext();
            Context envContext = (Context) ctx
                    .lookup("java:/comp/env");
            dataFactory = (DataSource) envContext
                    .lookup("jdbc/oracle");
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public List<ArticleVO> selectAllArticles(
            Map<String, Integer> pagingMap) {

        List articlesList = new ArrayList();
        int section = (Integer)pagingMap.get("section");
        int pageNum = (Integer)pagingMap.get("pageNum");
        try{
            conn = dataFactory.getConnection();
            String query = "SELECT * FROM ("
                    + "SELECT ROWNUM AS recNum, LVL, articleNO, parentNO, title, id, writeDate"
                    + " FROM (SELECT LEVEL AS LVL, articleNO, parentNO, title, id, writeDate"
                        + " FROM t_board"
                        + " START WITH parentNO=0"
                        + " CONNECT BY PRIOR articleNO = parentNO"
                        + " ORDER SIBLINGS BY articleNO DESC)"
                    + ")"
                    + " WHERE recNum BETWEEN(?-1)*100+(?-1)*10+1 AND (?-1)*100+?*10";
            System.out.println(query);
            pstmt = conn.prepareStatement(query);
            pstmt.setInt(1,section);
            pstmt.setInt(2,pageNum);
            pstmt.setInt(3,section);
            pstmt.setInt(4,pageNum);
            ResultSet rs = pstmt.executeQuery();
            while(rs.next()){
                int level = rs.getInt("lvl");
                int articleNO = rs.getInt("articleNO");
                int parentNO = rs.getInt("parentNO");
                String title = rs.getString("title");
                String id = rs.getString("writeDate");
                Date writeDate = rs.getDate("writeDate");
                ArticleVO article = new ArticleVO();
                article.setLevel(level);
                article.setArticleNO(articleNO);
                article.setParentNO(parentNO);
                article.setTitle(title);
                article.setId(id);
                article.setWriteDate(writeDate);
                articlesList.add(article);
            }
            rs.close();
            pstmt.close();
            conn.close();
        } catch (Exception e){
            e.printStackTrace();
        }
        return articlesList;
    }

    public int selectTotArticles() {
        try{
            conn = dataFactory.getConnection();
            //행 전체 개수 쿼리
            String query = "SELECT COUNT(articleNO) FROM t_board ";
            System.out.println(query);
            pstmt = conn.prepareStatement(query);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()){
                return (rs.getInt(1));
            }
            rs.close();
            pstmt.close();
            conn.close();
        } catch (Exception e){
            e.printStackTrace();
        }
        return 0;
    }

    public int insertNewArticle(ArticleVO article) {
        int articleNO = getNewArticleNO();
        try{
            conn = dataFactory.getConnection();
            int parentNO = article.getParentNO();
            String title = article.getTitle();
            String content = article.getContent();
            String id = article.getId();
            String imageFileName = article.getImageFileName();
            String query = "INSERT INTO t_board (articleNO, parentNO, title, content, imageFileName, id)"
                    + " VALUES (?, ?, ?, ?, ?, ?)";
            System.out.println(query);
            pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, articleNO);
            pstmt.setInt(2, parentNO);
            pstmt.setString(3, title);
            pstmt.setString(4, content);
            pstmt.setString(5, imageFileName);
            pstmt.setString(6, id);
            pstmt.executeUpdate();
            pstmt.close();
            conn.close();
        } catch (Exception e){
            e.printStackTrace();
        }
        return articleNO;
    }

    private int getNewArticleNO() {
        try {
            conn = dataFactory.getConnection();
            String query = "SELECT MAX(articleNO) FROM t_board";
            System.out.println(query);
            pstmt = conn.prepareStatement(query);
            ResultSet rs = pstmt.executeQuery(query);
            if (rs.next()){
                return (rs.getInt(1) + 1);
            }
            rs.close();
            pstmt.close();
            conn.close();
        } catch (Exception e){
            e.printStackTrace();
        }
        return 0;
    }
}
