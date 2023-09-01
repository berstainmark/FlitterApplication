package org.flitter.app.currency.mat.dao;

import org.flitter.app.currency.mat.model.BuyMat;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface BuyMatRepository extends CrudRepository<BuyMat, Long> {

    @Query("select a from BuyMat a where a.xuuid=:xuuid")
    Optional<BuyMat> findBuyId(@Param("xuuid") String xuuid);

    @Query("select a from BuyMat a where a.iscloseexternal=:iscloseexternal")
    List<BuyMat> findByCloseExternal(@Param("iscloseexternal") Integer iscloseexternal);

    @Query("select a from BuyMat a where a.isclose=:isclose AND a.maxprice<:maxprice")
    List<BuyMat> findBuyMaxPrice(@Param("isclose") Integer isclose, @Param("maxprice") Double maxprice);

    @Query("select a from BuyMat a where a.isclose=:isclose AND (a.tradeprice-:margin)>:price")
    List<BuyMat> findMarginPrice(@Param("isclose") Integer isclose, @Param("price") Double price, @Param("margin") Double margin);


}
