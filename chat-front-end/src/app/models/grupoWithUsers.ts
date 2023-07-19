import { User } from "./user";

export interface GrupoWithUsers{

    idGrupo: number;
    nome: string;
    users: User[];
}