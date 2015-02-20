<?php
namespace Insalade\UserBundle\Listener;

use Doctrine\ORM\Event\LifecycleEventArgs;
use Insalade\UserBundle\Entity\User;

class RegistrationListener
{
    public function postPersist(LifecycleEventArgs $args)
    {
        $entity = $args->getEntity();
        $entityManager = $args->getEntityManager();

        // peut-être voulez-vous seulement agir sur une entité « Product »
        if ($entity instanceof User) {
            $entity->setEnabled(false);
        }
        $entityManager->flush();
    }
}