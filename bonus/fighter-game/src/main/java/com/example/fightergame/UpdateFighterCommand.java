package com.example.fightergame;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateFighterCommand {

    private String name;
    private int stamina;
    private int damage;
}
