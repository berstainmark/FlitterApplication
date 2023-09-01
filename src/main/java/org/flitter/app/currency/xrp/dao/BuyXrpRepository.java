package org.flitter.app.currency.xrp.dao;

import org.flitter.app.currency.xrp.model.BuyXrp;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface BuyXrpRepository extends CrudRepository<BuyXrp, Long> {

    @Query("select a from BuyXrp a where a.xuuid=:xuuid")
    Optional<BuyXrp> findBuyId(@Param("xuuid") String xuuid);

    @Query("select a from BuyXrp a where a.iscloseexternal=:iscloseexternal")
    List<BuyXrp> findByCloseExternal(@Param("iscloseexternal") Integer iscloseexternal);

    @Query("select a from BuyXrp a where a.isclose=:isclose AND a.maxprice<:maxprice")
    List<BuyXrp> findBuyMaxPrice(@Param("isclose") Integer isclose, @Param("maxprice") Double maxprice);

    @Query("select a from BuyXrp a where a.isclose=:isclose AND (a.tradeprice-:margin)>:price")
    List<BuyXrp> findMarginPrice(@Param("isclose") Integer isclose, @Param("price") Double price, @Param("margin") Double margin);


}
