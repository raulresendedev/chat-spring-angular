import { User } from "./user";

export interface GrupoWithUsers{

    idGrupo: number;
    nome: string;
    usuarioAcao: string;
    users: User[];
}