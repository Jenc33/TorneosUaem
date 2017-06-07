/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jpa.entities;

import java.io.Serializable;
import java.util.Collection;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author ingjo
 */
@Entity
@Table(name = "EQUIPS")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Equips.findAll", query = "SELECT e FROM Equips e")
    , @NamedQuery(name = "Equips.findById", query = "SELECT e FROM Equips e WHERE e.id = :id")
    , @NamedQuery(name = "Equips.findByIdCapitain", query = "SELECT e FROM Equips e WHERE e.idCapitain = :idCapitain")
    , @NamedQuery(name = "Equips.findByName", query = "SELECT e FROM Equips e WHERE e.name = :name")})
public class Equips implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "ID")
    private Integer id;
    @Basic(optional = false)
    @NotNull
    @Column(name = "ID_CAPITAIN")
    private int idCapitain;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 50)
    @Column(name = "NAME")
    private String name;
    @JoinColumn(name = "ID_TOURNAMENT", referencedColumnName = "ID")
    @ManyToOne(optional = false)
    private Tournaments idTournament;
    @OneToMany(mappedBy = "idEquip")
    private Collection<Students> studentsCollection;

    public Equips() {
    }

    public Equips(Integer id) {
        this.id = id;
    }

    public Equips(Integer id, int idCapitain, String name) {
        this.id = id;
        this.idCapitain = idCapitain;
        this.name = name;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public int getIdCapitain() {
        return idCapitain;
    }

    public void setIdCapitain(int idCapitain) {
        this.idCapitain = idCapitain;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Tournaments getIdTournament() {
        return idTournament;
    }

    public void setIdTournament(Tournaments idTournament) {
        this.idTournament = idTournament;
    }

    @XmlTransient
    public Collection<Students> getStudentsCollection() {
        return studentsCollection;
    }

    public void setStudentsCollection(Collection<Students> studentsCollection) {
        this.studentsCollection = studentsCollection;
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
        if (!(object instanceof Equips)) {
            return false;
        }
        Equips other = (Equips) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "jpa.entities.Equips[ id=" + id + " ]";
    }
    
}
