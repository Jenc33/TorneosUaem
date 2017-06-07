/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jpa.entities;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author ingjo
 */
@Entity
@Table(name = "TORUNAMENTS")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Torunaments.findAll", query = "SELECT t FROM Torunaments t")
    , @NamedQuery(name = "Torunaments.findById", query = "SELECT t FROM Torunaments t WHERE t.id = :id")
    , @NamedQuery(name = "Torunaments.findByName", query = "SELECT t FROM Torunaments t WHERE t.name = :name")
    , @NamedQuery(name = "Torunaments.findByDescription", query = "SELECT t FROM Torunaments t WHERE t.description = :description")
    , @NamedQuery(name = "Torunaments.findByInscriptionStartDate", query = "SELECT t FROM Torunaments t WHERE t.inscriptionStartDate = :inscriptionStartDate")
    , @NamedQuery(name = "Torunaments.findByInscriptionEndDate", query = "SELECT t FROM Torunaments t WHERE t.inscriptionEndDate = :inscriptionEndDate")
    , @NamedQuery(name = "Torunaments.findByTournamentStartDate", query = "SELECT t FROM Torunaments t WHERE t.tournamentStartDate = :tournamentStartDate")
    , @NamedQuery(name = "Torunaments.findByTounamentEndDate", query = "SELECT t FROM Torunaments t WHERE t.tounamentEndDate = :tounamentEndDate")})
public class Torunaments implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "ID")
    private Integer id;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 20)
    @Column(name = "NAME")
    private String name;
    @Size(max = 500)
    @Column(name = "DESCRIPTION")
    private String description;
    @Basic(optional = false)
    @NotNull
    @Column(name = "INSCRIPTION_START_DATE")
    @Temporal(TemporalType.DATE)
    private Date inscriptionStartDate;
    @Basic(optional = false)
    @NotNull
    @Column(name = "INSCRIPTION_END_DATE")
    @Temporal(TemporalType.DATE)
    private Date inscriptionEndDate;
    @Basic(optional = false)
    @NotNull
    @Column(name = "TOURNAMENT_START_DATE")
    @Temporal(TemporalType.DATE)
    private Date tournamentStartDate;
    @Basic(optional = false)
    @NotNull
    @Column(name = "TOUNAMENT_END_DATE")
    @Temporal(TemporalType.DATE)
    private Date tounamentEndDate;
    @JoinColumn(name = "ID_DISCIPLINA", referencedColumnName = "ID")
    @ManyToOne(optional = false)
    private Disciplines idDisciplina;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "idTournament")
    private Collection<Equips> equipsCollection;

    public Torunaments() {
    }

    public Torunaments(Integer id) {
        this.id = id;
    }

    public Torunaments(Integer id, String name, Date inscriptionStartDate, Date inscriptionEndDate, Date tournamentStartDate, Date tounamentEndDate) {
        this.id = id;
        this.name = name;
        this.inscriptionStartDate = inscriptionStartDate;
        this.inscriptionEndDate = inscriptionEndDate;
        this.tournamentStartDate = tournamentStartDate;
        this.tounamentEndDate = tounamentEndDate;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getInscriptionStartDate() {
        return inscriptionStartDate;
    }

    public void setInscriptionStartDate(Date inscriptionStartDate) {
        this.inscriptionStartDate = inscriptionStartDate;
    }

    public Date getInscriptionEndDate() {
        return inscriptionEndDate;
    }

    public void setInscriptionEndDate(Date inscriptionEndDate) {
        this.inscriptionEndDate = inscriptionEndDate;
    }

    public Date getTournamentStartDate() {
        return tournamentStartDate;
    }

    public void setTournamentStartDate(Date tournamentStartDate) {
        this.tournamentStartDate = tournamentStartDate;
    }

    public Date getTounamentEndDate() {
        return tounamentEndDate;
    }

    public void setTounamentEndDate(Date tounamentEndDate) {
        this.tounamentEndDate = tounamentEndDate;
    }

    public Disciplines getIdDisciplina() {
        return idDisciplina;
    }

    public void setIdDisciplina(Disciplines idDisciplina) {
        this.idDisciplina = idDisciplina;
    }

    @XmlTransient
    public Collection<Equips> getEquipsCollection() {
        return equipsCollection;
    }

    public void setEquipsCollection(Collection<Equips> equipsCollection) {
        this.equipsCollection = equipsCollection;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Torunaments)) {
            return false;
        }
        Torunaments other = (Torunaments) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "jpa.entities.Torunaments[ id=" + id + " ]";
    }
    
}
