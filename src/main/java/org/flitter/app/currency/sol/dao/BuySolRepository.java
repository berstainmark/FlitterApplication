package org.flitter.app.currency.sol.dao;

import org.flitter.app.currency.sol.model.BuySol;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface BuySolRepository extends CrudRepository<BuySol, Long> {

    @Query("select a from BuySol a where a.xuuid=:xuuid")
    Optional<BuySol> findBuyId(@Param("xuuid") String xuuid);

    @Query("select a from BuySol a where a.iscloseexternal=:iscloseexternal")
    List<BuySol> findByCloseExternal(@Param("iscloseexternal") Integer iscloseexternal);

    @Query("select a from BuySol a where a.isclose=:isclose AND a.maxprice<:maxprice")
    List<BuySol> findBuyMaxPrice(@Param("isclose") Integer isclose, @Param("maxprice") Double maxprice);

    @Query("select a from BuySol a where a.isclose=:isclose AND (a.tradeprice-:margin)>:price")
    List<BuySol> findMarginPrice(@Param("isclose") Integer isclose, @Param("price") Double price, @Param("margin") Double margin);


}
