package com.traders.exchange.domain;

import jakarta.persistence.*;
import org.hibernate.annotations.Where;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "portfolio")
//have constraints on DB side
public class Portfolio  extends AbstractAuditingEntity<Long> implements Serializable {

    public Portfolio(){

    }

    public Portfolio(Long userId){
        this.userId = userId;
    }
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;



//    @OneToOne
//    @JoinColumn(name = "user_id", referencedColumnName = "id")
    //give manual foreign key
    private long userId;

    @OneToMany(mappedBy = "portfolio", cascade = CascadeType.ALL, orphanRemoval = true)
  //  @Where(clause = "quantity != 0")
    private Set<PortfolioStock> portfolioStocks = new HashSet<>();

//    public User getUser() {
//        return user;
//    }
//
//    public void setUser(User user) {
//        this.user = user;
//    }


    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public Set<PortfolioStock> getStocks() {
        return portfolioStocks;
    }

    public void setStocks(Set<PortfolioStock> portfolioStocks) {
        this.portfolioStocks = portfolioStocks;
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

}
