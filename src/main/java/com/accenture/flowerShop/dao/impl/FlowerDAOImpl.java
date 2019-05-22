package com.accenture.flowerShop.dao.impl;

import com.accenture.flowerShop.dao.FlowerDAO;
import com.accenture.flowerShop.entity.flower.Flower;
import com.accenture.flowerShop.entity.flower.Stock;
import com.accenture.flowerShop.model.FlowerInfo;
import com.accenture.flowerShop.model.PaginationResult;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.transaction.Transactional;
import java.util.List;

@Transactional
public class FlowerDAOImpl implements FlowerDAO {
    @Autowired
    private SessionFactory sessionFactory;

    @Override
    public Flower findFlower(String name){

        Session session =sessionFactory.getCurrentSession();
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<Flower> criteria = builder.createQuery(Flower.class);
        Root<Flower> root = criteria.from(Flower.class);
        criteria.select(root).where(builder.equal(root.get("name"), name));
        Query<Flower> query = session.createQuery(criteria);

        return query.getSingleResult();
    }
    @Override
    public List<Flower> getAllFlowers(){
        Session session =sessionFactory.getCurrentSession();
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<Flower> criteria = builder.createQuery(Flower.class);
        Root<Flower> root = criteria.from(Flower.class);
        criteria.select(root);
        Query<Flower> query = session.createQuery(criteria);
        return query.getResultList();
    }
    @Override
    public List<Stock> getAllStocks(){
        Session session =sessionFactory.getCurrentSession();
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<Stock> criteria = builder.createQuery(Stock.class);
        Root<Stock> root = criteria.from(Stock.class);
        criteria.select(root);
        Query<Stock> query = session.createQuery(criteria);
        return query.getResultList();
    }
    @Override
    public boolean updateStocks(List<Stock> stocks) throws Exception{
        Session session =sessionFactory.getCurrentSession();
        for(Stock stock : stocks){
            session.update(stock);
        }
        return true;
    }
    /*@Override
    public FlowerInfo getFlowerInfo(String name){
        Flower flower = this.findFlower(name);
        if(flower == null){
            return null;
        }
        return new FlowerInfo(
                flower.getName(),
                flower.getPrice(),
                flower.getStock().getQuantity());
    }*/

    @Override
    public PaginationResult<FlowerInfo> queryFlowers(
            int page,
            int maxResult,
            int maxNavigationPage,
            String likeName,
            double priceMin,
            double priceMax
    ){

        String sql = "Select new " + FlowerInfo.class.getName() //
                + "( p.name, p.price, q.quantity) " + " from "//
                + Flower.class.getName() + " p ,"
                + Stock.class.getName() + " q " +
                "where lower(p.name) = lower(q.name) ";
        if (likeName != null && likeName.length() > 0) {
            sql += " and lower(p.name) like :likeName ";
        }
        if (priceMin > 0 ){
            sql += "and p.price > " + priceMin;
        }
        if (priceMax > 0 && priceMax > priceMin){
            sql += "and p.price < " + priceMax;
        }
        sql += " order by p.price desc ";
        Session session = sessionFactory.getCurrentSession();
        Query query = session.createQuery(sql);
        if (likeName != null && likeName.length() > 0) {
            query.setParameter("likeName", "%" + likeName.toLowerCase() + "%");
        }
        return new PaginationResult<FlowerInfo>(query, page, maxResult, maxNavigationPage);
    }
}
