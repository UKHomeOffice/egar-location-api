package uk.gov.digital.ho.egar.location.service.repository.model;

import java.time.ZonedDateTime;
import java.util.UUID;

import javax.persistence.Index;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import org.springframework.validation.annotation.Validated;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.AccessLevel;
import uk.gov.digital.ho.egar.location.model.GeographicLocation;
import uk.gov.digital.ho.egar.location.model.GeographicLocationWithUuid;
import uk.gov.digital.ho.egar.location.model.GeographicPoint;
import uk.gov.digital.ho.egar.location.model.Latlong;

/**
 * This is the main storage object.
 * It is both the persistence object and the object sent out as part of the Rest interface.
 * Because it is part of the RREST it implements <code>GeographicLocationWithUuid</code>.
 * Methods such as {@link #getPoint()} are implemented as a facade and exposes data from within the persistence layer without the need to copy.
 * Methods such as {@link #getLatlong()} are also facades as they are part of {@link #getPoint()}.
 * They serve an additional purpose of assisting the object validation.
 * 
 */
@Data
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@Builder
@EqualsAndHashCode(callSuper = false)
@Entity
@Table( name= "Location"
		,
		indexes = @Index(name="userIdx",columnList="userUuid")
		)
@Validated 
public class GeographicLocationPersistentRecord implements GeographicLocationWithUuid {

	@Id
	private UUID locationUuid ;

	private UUID userUuid ;
	/**
	 * This is the time the flight is at this location.
	 */
	private ZonedDateTime dateTimeAt ;
	
	private String icaoCode ;
	private String iataCode ;
	@JsonIgnore
	private String latitude ;
	@JsonIgnore
	private String longitude ;
	

	public GeographicLocationPersistentRecord copy( GeographicLocationWithUuid src ) 
	{
		this.setUserUuid(src.getUserUuid());		
		
		return copy((GeographicLocation)src) ;
	}
	public GeographicLocationPersistentRecord copy( GeographicLocation src ) 
	{
		this.setDateTimeAt(src.getDateTimeAt());
		this.setIcaoCode(src.getIcaoCode());
		this.setIataCode(src.getIataCode());
		if ( src.getLatlong() != null )
		{
			this.setLatitude(src.getLatlong().getLatitude());
			this.setLongitude(src.getLatlong().getLongitude());
		}
		
		return this ;
	}	
	
	/**
	 * Populate the Persistent data as {@link #getPoint()} is a facade.
	 * @param val the new values.
	 */
	public void setPoint(GeographicPoint val) {
		if ( val == null )
		{
			GeographicLocationPersistentRecord.this.latitude = null ;
			GeographicLocationPersistentRecord.this.longitude = null ;
		}
		else
		{
			GeographicLocationPersistentRecord.this.latitude = val.getLatitude() ;
			GeographicLocationPersistentRecord.this.longitude = val.getLongitude() ;
		}
	}
	
	/**
	 * This is data implemented as a facade and exposes data from within the persistence layer without the need to copy.
	 * This is for use in the REST layer.
	 */
	@Override
	@Transient
	public GeographicPoint getPoint() {
		return !hasLatLong()
			   ? null
			   :new GeographicPoint()
				{

					@Override 
					public String getLatitude() {
						return GeographicLocationPersistentRecord.this.latitude;
					}

					@Override
					public String getLongitude() {
						return GeographicLocationPersistentRecord.this.longitude;
					}
			
				};
	}


	/**
	 * A facade object to assist {@link #getPoint()}.
	 */
	@Override
	@Transient
	public Latlong getLatlong()  {
		return !hasLatLong()
				? null
				: new Latlong()
				{
					@Override 
					public String getLatitude() {
						return GeographicLocationPersistentRecord.this.latitude;
					}
		
					@Override
					public String getLongitude() {
						return GeographicLocationPersistentRecord.this.longitude;
					}			
				};
	}


	protected boolean hasLatLong() {
		return (this.latitude!=null) || (this.longitude!=null);
	}


    /**
     * Clears the location specific information from a location persistent record
     */
	public void clear() {
		this.setDateTimeAt(null);
		this.setIcaoCode(null);
		this.setIataCode(null);
		this.setLatitude(null);
		this.setLongitude(null);
	}
}
