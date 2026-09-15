package uk.ac.newcastle.enterprisemiddleware.flight;

import javax.enterprise.context.ApplicationScoped;

import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;

/**
 * <p>Panache-backed repository over the existing {@link Flight} entity, used
 * solely to obtain a {@link io.quarkus.hibernate.orm.panache.PanacheQuery}
 * that can be paginated with the reusable
 * {@link org.acme.infrastructure.api.pagination.PagedResult} utility.</p>
 *
 * <p>{@link Flight} does not need to change (it is not a {@code PanacheEntity})
 * for this to work: {@link PanacheRepositoryBase} works with any existing
 * JPA entity given its id type.</p>
 */
@ApplicationScoped
public class FlightPanacheRepository implements PanacheRepositoryBase<Flight, Long> {
}
