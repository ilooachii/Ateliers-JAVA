package domaine;

import util.Util;

import java.time.Duration;
import java.util.*;

public class Plat {

    public enum Difficulte {
        X, XX, XXX, XXXX, XXXXX;

        @Override
        public String toString() {
            return super.toString().replace("X", "*");
        }
    }

    public enum Cout {
        $, $$, $$$, $$$$, $$$$$;

        @Override
        public String toString() {
            return super.toString().replace("$", "€");
        }
    }

    public enum Type {
        ENTREE("Entrée"), PLAT("Plat"), DESSERT("Dessert");

        String nom;

        Type(String nom) {
            this.nom=nom;
        }

        public String getNom() {
            return nom;
        }

        @Override
        public String toString() {
            return nom;
        }
    }

    private final String nom;
    private int nbPersonnes;
    private Difficulte niveauDeDifficulte;
    private Cout cout;
    private Duration dureeEnMinutes = Duration.ofMinutes(0);
    private Type type;
    private List<Instruction> recette = new ArrayList<Instruction>();
    private Set<IngredientQuantifie> ingredients = new HashSet<>();

    public Plat(String nom, int nbPersonnes, Difficulte niveauDeDifficulte, Cout cout, Type type) {
        Util.checkString(nom);
        Util.checkStrictlyPositive(nbPersonnes);
        Util.checkObject(niveauDeDifficulte);
        Util.checkObject(cout);
        Util.checkObject(type);
        this.nom = nom;
        this.nbPersonnes = nbPersonnes;
        this.niveauDeDifficulte = niveauDeDifficulte;
        this.cout = cout;
        this.type = type;
    }

    public String getNom() {
        return nom;
    }

    public int getNbPersonnes() {
        return nbPersonnes;
    }

    public Difficulte getNiveauDeDifficulte() {
        return niveauDeDifficulte;
    }

    public Cout getCout() {
        return cout;
    }

    public Duration getDureeEnMinutes() {
        return dureeEnMinutes;
    }

    public Type getType() {
        return type;
    }

    /**
     * Cette méthode insère l'instruction à la position précisée (la position
     * commence à 1)
     * 
     * @param position    la position à laquelle l'instruction doit être insérée
     * @param instruction l'instruction à insérer
     * @throws IllegalArgumentException en cas de position invalide ou d'instruction
     *                                  null
     */
    public void insererInstruction(int position, Instruction instruction) {
        Util.checkStrictlyPositive(position);
        Util.checkObject(instruction);
        if (position > recette.size() + 1)
            throw new IllegalArgumentException();
        recette.add(position - 1, instruction);
        dureeEnMinutes = dureeEnMinutes.plus(instruction.getDureeEnMinutes());
    }

    /**
     * Cette méthode ajoute l'instruction en fin de la liste
     * 
     * @param instruction l'instruction à ajouter
     * @throws IllegalArgumentException en cas d'instruction null
     */
    public void ajouterInstruction(Instruction instruction) {
        Util.checkObject(instruction);
        recette.add(instruction);
        dureeEnMinutes = dureeEnMinutes.plus(instruction.getDureeEnMinutes());
    }

    /**
     * Cette méthode remplace l’instruction de la position précisée par celle en
     * paramètre (la position commence à 1).
     * 
     * @param position    la position de l'instruction à remplacer
     * @param instruction la nouvelle instruction
     * @return l'instruction remplacée
     * @throws IllegalArgumentException en cas de position invalide ou d'instruction
     *                                  null
     */
    public Instruction remplacerInstruction(int position, Instruction instruction) {
        Util.checkStrictlyPositive(position);
        Util.checkObject(instruction);
        if (position > recette.size())
            throw new IllegalArgumentException();
        Instruction instructionRemplacee = recette.set(position - 1, instruction);
        dureeEnMinutes = dureeEnMinutes.minus(instructionRemplacee.getDureeEnMinutes());
        dureeEnMinutes = dureeEnMinutes.plus(instruction.getDureeEnMinutes());
        return instructionRemplacee;
    }

    /**
     * Cette méthode supprime l’instruction qui se trouve à la position précisée en
     * paramètre (la position commence à 1).
     * 
     * @param position la position de l'instruction à supprimer
     * @return l'instuction supprimée
     * @throws IllegalArgumentException en cas de position invalide
     */
    public Instruction supprimerInstruction(int position) {
        Util.checkStrictlyPositive(position);
        if (position > recette.size())
            throw new IllegalArgumentException();
        Instruction instructionSupprimee = recette.remove(position - 1);
        dureeEnMinutes = dureeEnMinutes.minus(instructionSupprimee.getDureeEnMinutes());
        return instructionSupprimee;
    }

    public List<Instruction> instructions() {
        return Collections.unmodifiableList(recette);
    }

    public boolean ajouterIngredient(Ingredient ingredient, int quantite, Unite unite) {
        Util.checkObject(ingredient);
        Util.checkObject(unite);
        Util.checkStrictlyPositive(quantite);
        IngredientQuantifie ing = new IngredientQuantifie(ingredient, quantite, unite);
        return ingredients.add(ing);
    }

    public boolean ajouterIngredient(Ingredient ingredient, int quantite) {
        Util.checkObject(ingredient);
        Util.checkStrictlyPositive(quantite);
        IngredientQuantifie ing = new IngredientQuantifie(ingredient, quantite, Unite.NEANT);
        return ingredients.add(ing);
    }

    public boolean modifierIngredient(Ingredient ingredient, int quantite, Unite unite) {
        Util.checkObject(ingredient);
        Util.checkObject(unite);
        Util.checkStrictlyPositive(quantite);
        IngredientQuantifie ing = trouverIngredientQuantifie(ingredient);
        if (ing == null) return false;
    
        ing.setQuantite(quantite);
        ing.setUnite(unite);
        return true;
    }

    public boolean supprimerIngredient(Ingredient ingredient) {
        Util.checkObject(ingredient);
        IngredientQuantifie ingASupp = null;
        for (IngredientQuantifie ingredientQuantifie : ingredients) {
            if (ingredientQuantifie.getIngredient().equals(ingredient)) {
                ingASupp = ingredientQuantifie;
                break;
            }
        }
        if (ingASupp == null) {
            return false;
        }
        return ingredients.remove(ingASupp);
    }

    public IngredientQuantifie trouverIngredientQuantifie(Ingredient ingredient) {
        Util.checkObject(ingredient);
        IngredientQuantifie ing = null;
        for (IngredientQuantifie ingredientQuantifie : ingredients) {
            if (ingredientQuantifie.getIngredient().equals(ingredient)) {
                ing = ingredientQuantifie;
                break;
            }
        }
        if (ing == null) {
            return null;
        }
        return ing;
    }

    @Override
    public String toString() {
        String hms = String.format("%d h %02d m", dureeEnMinutes.toHours(), dureeEnMinutes.toMinutesPart());
        String res = this.nom + "\n\n";
        res += "Pour " + this.nbPersonnes + " personnes\n";
        res += "Difficulté : " + this.niveauDeDifficulte + "\n";
        res += "Coût : " + this.cout + "\n";
        res += "Durée : " + hms + " \n\n";
        res += "Ingrédients :\n";
        for (IngredientQuantifie ingredientQuantifie : this.ingredients) {
            res += ". " + ingredientQuantifie + "\n";
        }
        int i = 1;
        for (Instruction instruction : this.recette) {
            res += i++ + ". " + instruction + "\n";
        }
        return res;
    }
}
